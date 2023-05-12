package com.gtpuser.gtpuser.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import com.gtpuser.gtpuser.models.User;
import com.gtpuser.gtpuser.repo.UserRepo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

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

			String id = signedJWT.getPayload().toJSONObject().get("sub").toString();
			UUID userId = UUID.fromString(id);
			return userRepo.findById(userId)
					.publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic());

		} catch (Exception e) {
			return Mono.just(userEntity);
		}
	}
}
