package com.javatechie.jwt.api.controller;

import com.javatechie.jwt.api.entity.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.javatechie.jwt.api.entity.AuthRequest;
import com.javatechie.jwt.api.entity.JwtResponse;
import com.javatechie.jwt.api.entity.RefreshToken;
import com.javatechie.jwt.api.service.RefreshTokenService;
import com.javatechie.jwt.api.util.JwtUtil;

@RestController
public class WelcomeController {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private RefreshTokenService refreshTokenService;


	@GetMapping("/")
	public String welcome() {
		return "Welcome to javatechie !!";
	}

	@PostMapping("/api/auth/authenticate")
	public JwtResponse generateToken(@RequestBody AuthRequest authRequest) throws Exception {

		Authentication authentication =  authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
		if (authentication.isAuthenticated()) {
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUserName());

			JwtResponse jwtResponse = JwtResponse.builder()
					.accessToken(jwtUtil.generateToken(authRequest.getUserName()))
					.token(refreshToken.getToken())
					.build();
			return jwtResponse;

		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}

	}

	@PostMapping("/api/auth/refreshToken")
	public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return refreshTokenService.findByToken(refreshTokenRequest.getToken())
				.map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUserInfo)
				.map(userInfo -> {
					String accessToken = jwtUtil.generateToken(userInfo.getUserName());
					return JwtResponse.builder()
							.accessToken(accessToken)
							.token(refreshTokenRequest.getToken())
							.build();
				}).orElseThrow(() -> new RuntimeException(
						"Refresh token is not in database!"));
	}
}