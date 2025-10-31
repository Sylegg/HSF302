package fa25.beitweb.service;

import fa25.beitweb.entity.User;
import fa25.beitweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUsersByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean exsitingEmail(String email) {
        return userRepository.findByEmail(email)!= null;
    }
}
