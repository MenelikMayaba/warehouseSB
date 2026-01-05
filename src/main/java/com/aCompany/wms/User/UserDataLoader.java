package com.aCompany.wms.User;

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
            admin.setRoles(Set.of(Role.ADMIN, Role.PICKER, Role.PACKER, Role.DISPATCHER, Role.RECEIVER));
            userRepository.save(admin);

            User picker = new User();
            picker.setUsername("picker");
            picker.setPassword(passwordEncoder.encode("picker123"));
            picker.setRoles(Set.of(Role.PICKER));
            userRepository.save(picker);

            User packer = new User();
            packer.setUsername("packer");
            packer.setPassword(passwordEncoder.encode("packer123"));
            packer.setRoles(Set.of(Role.PACKER));
            userRepository.save(packer);

            User dispatcher = new User();
            dispatcher.setUsername("dispatcher");
            dispatcher.setPassword(passwordEncoder.encode("dispatcher123"));
            dispatcher.setRoles(Set.of(Role.DISPATCHER));
            userRepository.save(dispatcher);

            User receiver = new User();
            receiver.setUsername("receiver");
            receiver.setPassword(passwordEncoder.encode("receiver123"));
            receiver.setRoles(Set.of(Role.RECEIVER));
            userRepository.save(receiver);

        }
    }
}

