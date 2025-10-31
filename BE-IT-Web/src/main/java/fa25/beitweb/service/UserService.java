package fa25.beitweb.service;

import fa25.beitweb.entity.User;

import java.util.List;

public interface UserService {
    public void createUser(User user);
    public User getUsersByEmailAndPassword(String email,String password);
    List<User> getAllUsers();
    public User getUserById(int id);
    public boolean exsitingEmail(String email);
}
