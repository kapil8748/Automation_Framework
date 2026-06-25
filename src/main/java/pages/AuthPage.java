package pages;

public class AuthPage{

    public boolean login( String username , String password){
        if ( username== null || password== null){
            throw new IllegalArgumentException("Credentials cannot be null");
            
        }
        return "Admin".equals(username) && "Secret123".equals(password);
        
    }
    

}