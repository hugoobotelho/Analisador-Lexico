public class Token {
    private String token;
    private String lexema;

    public Token (String lexema, String token) {
        this.token = token;
        this.lexema = lexema;
    }

    
    public String getToken() {
        return token;
    }

    public String getLexema() {
        return lexema;
    }

    @Override
    public String toString() {
        return "Token{" + "token='" + token + "', lexema='" + lexema + "'}";
    }

}