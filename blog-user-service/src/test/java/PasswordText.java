import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordText {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password=encoder.encode("1111");
        System.out.println(password);
    }
}
