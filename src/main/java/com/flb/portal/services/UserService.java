package com.flb.portal.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import com.flb.portal.models.Permiso;
import com.flb.portal.models.Usuario;

public class UserService implements UserDetailsService {

    @Value("${url.seguridad.rest.service}")
    String urlUsuario;

    @Autowired
    RestTemplate restTemplate;

    public Usuario findByname(String username) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        //aqui d aigual la contraseña
        params.put("password", "prueba");

        Usuario u = restTemplate.getForObject(urlUsuario + "usuario/buscar?username={username}&password={password}",
                Usuario.class, params);
        return u;
    }

    public Usuario findBynameAndPassword(String username, String password) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        Usuario u = restTemplate.getForObject(urlUsuario + "usuario/buscar?username={username}&password={password}",
                Usuario.class, params);
        return u;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = findByname(username);
        List<Permiso> permissions = u.getPermisos();

        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();

        for (Permiso permission : permissions) {
            roles.add(new SimpleGrantedAuthority(permission.getNombre()));
        }

        UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username(u.getNombre())
                .password(u.getPassword())
                .authorities(roles)
                .build();
        return user;
    }

}
