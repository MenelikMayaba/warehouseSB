package com.aCompany.wms.entity;

import com.aCompany.wms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of("ADMIN","PICKER","PACKER","DISPATCHER"));
            userRepository.save(admin);

            User picker = new User();
            picker.setUsername("picker");
            picker.setPassword(passwordEncoder.encode("picker123"));
            picker.setRoles(Set.of("PICKER"));
            userRepository.save(picker);

            User packer = new User();
            packer.setUsername("packer");
            packer.setPassword(passwordEncoder.encode("packer123"));
            packer.setRoles(Set.of("PACKER"));
            userRepository.save(packer);

            User dispatcher = new User();
            dispatcher.setUsername("dispatcher");
            dispatcher.setPassword(passwordEncoder.encode("dispatcher123"));
            dispatcher.setRoles(Set.of("DISPATCHER"));
            userRepository.save(dispatcher);
        }
    }
}

