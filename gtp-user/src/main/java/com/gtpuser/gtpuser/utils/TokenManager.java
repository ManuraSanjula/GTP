package com.gtpuser.gtpuser.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.repo.UserRepo;
import com.gtpuser.gtpuser.security.SecurityConstants;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenManager {
	public SignedJWT createSignedJWT(User user, String phoneNumber) throws NoSuchAlgorithmException, JOSEException {
		return getSignedJWT(createJWTClaimSet(user), phoneNumber);
	}

	public SignedJWT createSignedJWT(String phoneNumber) throws NoSuchAlgorithmException, JOSEException {
		return getSignedJWT(createJWTClaimSet(phoneNumber), phoneNumber);

	}

	private SignedJWT getSignedJWT(JWTClaimsSet jwtClaimSet2, String email) throws NoSuchAlgorithmException, JOSEException {
		JWTClaimsSet jwtClaimSet = jwtClaimSet2;
		KeyPair rsaKeys = generateKeyPair();
		JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic()).keyID(UUID.randomUUID().toString()).build();
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(jwk).type(JOSEObjectType.JWT).build(), jwtClaimSet);
		RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
		signedJWT.sign(signer);
		return signedJWT;
	}

	private JWTClaimsSet createJWTClaimSet(String phoneNumber) {
		return new JWTClaimsSet.Builder().subject(phoneNumber)
				.issuer("Manura Sanjula").issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME)).build();
	}

	private JWTClaimsSet createJWTClaimSet(User user) {

		return new JWTClaimsSet.Builder().subject(user.getUsername())
				.claim("authorities", user.getAuthorities().stream().collect(toList()))
				.issuer("Manura Sanjula").issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)).build();
	}

	private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

		generator.initialize(2048);
		return generator.genKeyPair();
	}

	public String encryptToken(SignedJWT signedJWT) throws JOSEException {
		DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());

		JWEObject jweObject = new JWEObject(
				new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).contentType("JWT").build(),
				new Payload(signedJWT));
		jweObject.encrypt(directEncrypter);

		return jweObject.serialize();
	}
	private final JwtConfiguration jwtConfiguration;
	private final UserRepo userRepo;

	public String decryptToken(String encryptedToken) throws ParseException, JOSEException {
		JWEObject jweObject = JWEObject.parse(encryptedToken);
		DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
		jweObject.decrypt(directDecrypter);
		return jweObject.getPayload().toSignedJWT().serialize();
	}

	private Mono<User> ifUserAbsentInCache(String id) {
		UUID userId = UUID.fromString(id);
		return userRepo.findById(userId).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
				.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {

				});
	}

	public Mono<User> validateTokenSignature(String token) {
		User userEntity = new User();
		userEntity.setId(null);
		try {
			String decryptUserToken = decryptToken(token);
			SignedJWT signedJWT = SignedJWT.parse(decryptUserToken);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			final Date expiration = claimsSet.getExpirationTime();
			Date todayDate = new Date();
			if (expiration.before(todayDate))
				return Mono.just(userEntity);
			RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
			if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
				return Mono.just(userEntity);

			String number = signedJWT.getPayload().toJSONObject().get("sub").toString();

			return userRepo.findByPhoneNumber(number)
					.publishOn(Schedulers.boundedElastic())
					//.filter(i-> i.getAuthorities() != null)
					.switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic());

		} catch (Exception e) {
			return Mono.just(userEntity);
		}
	}
}
