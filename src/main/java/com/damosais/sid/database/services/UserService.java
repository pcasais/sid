package com.damosais.sid.database.services;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.damosais.sid.database.beans.User;
import com.damosais.sid.database.dao.UserDAO;

/**
 * This class implements the methods required to access the User information from the database
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@Service
public class UserService {
    private static final String DEFAULT_NAME = "Admin";
    private static final String DEFAULT_PASS = "password";
    @Autowired
    private UserDAO userDao;

    /**
     * Creates a user with the given user name and password
     *
     * @param name
     *            The user's name
     * @param password
     *            The clear password
     * @throws GeneralSecurityException
     *             If there's a problem creating the user
     */
    public void create(String name, String password) throws GeneralSecurityException {
        final User user = new User();
        user.setName(name);
        user.setSalt(generateSalt());
        user.setPassword(getEncryptedPassword(password, user.getSalt()));
        userDao.save(user);
    }

    /**
     * Deletes an user from the database
     *
     * @param id
     *            The user ID being deleted
     */
    public void delete(long id) {
        final User user = new User();
        user.setId(id);
        userDao.delete(user);
    }

    /**
     * Returns a salt to be used for an encrypted password
     *
     * @return a salt to be used for an encrypted password
     * @throws NoSuchAlgorithmException
     *             If there's a problem with the algorithm to generate the salt
     */
    public String generateSalt() throws NoSuchAlgorithmException {
        // VERY important to use SecureRandom instead of just Random
        final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        
        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        final byte[] salt = new byte[8];
        random.nextBytes(salt);
        
        return new String(salt);
    }

    /**
     * Returns the encrypted version of the password
     *
     * @param password
     *            The original unencrypted password
     * @param salt
     *            The salt used during the encryption
     * @return the encrypted version of the password
     * @throws GeneralSecurityException
     *             If there is a problem encrypting the password
     */
    private String getEncryptedPassword(String password, String salt) throws GeneralSecurityException {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
        final String algorithm = "PBKDF2WithHmacSHA1";
        
        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        final int derivedKeyLength = 160;
        
        // Pick an iteration count that works for you. The NIST recommends at least 1,000 iterations:
        // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
        // iOS 4.x reportedly uses 10,000:
        // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
        final int iterations = 20000;
        
        final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, derivedKeyLength);

        final SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return new String(f.generateSecret(spec).getEncoded());
    }

    /**
     * Returns the existing users in the database
     *
     * @return The existing users in the database
     */
    public Iterable<User> list() {
        return userDao.findAll();
    }
    
    /**
     * Returns the user that matches the login credentials provided
     *
     * @param username
     *            The name of the user
     * @param password
     *            The password of the user
     * @return The matching user or null if none
     * @throws GeneralSecurityException
     *             If there's a problem with the password generation
     */
    public User login(String username, String password) throws GeneralSecurityException {
        // First of all we check that any user exists. If not we create the default one
        if (userDao.count() == 0) {
            create(DEFAULT_NAME, DEFAULT_PASS);
        }

        User user = userDao.findByName(username);
        // If the user exists but has either a blank password or the passwords don't match we don't return it
        if (user != null) {
            // If the user exists we encrypt the password to be able to compare them
            final String encryptedPassword = getEncryptedPassword(password, user.getSalt());
            if (StringUtils.isEmpty(user.getPassword()) || !user.getPassword().equals(encryptedPassword)) {
                user = null;
            }
        }
        return user;
    }

    /**
     * Saves an existing user to the database
     *
     * @param user
     *            The existing user
     */
    public void save(User user) {
        userDao.save(user);
    }
}