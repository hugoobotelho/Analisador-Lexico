import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AnalisadorLexico {

    private BufferedReader codigoPrograma;
    private ArrayList<String> marcadores = new ArrayList<>();
    private ArrayList<String> booleanos = new ArrayList<>();
    private ArrayList<String> palavrasReservadas = new ArrayList<>();
    private ArrayList<String> operadoresAritmeticos = new ArrayList<>();
    private ArrayList<String> operadoresRelacionais = new ArrayList<>();
    private ArrayList<String> atribuicoes = new ArrayList<>();
    private String numericos;
    private String identificadores;
    private String literais;
    private ArrayList<Token> tokens = new ArrayList<>();
    private StringBuilder codigoProgramaTeste = new StringBuilder();

    public AnalisadorLexico(String caminhoArquivo) {
        inicializarListasLexicasGlobais();
        String nomeRecursoNoJar = "/" + caminhoArquivo;
        InputStream inputStream = AnalisadorLexico.class.getResourceAsStream(nomeRecursoNoJar);

        if (inputStream == null) {
            System.out.println("Arquivo não encontrado (" + caminhoArquivo + ")!");
            throw new RuntimeException("Recurso não encontrado no JAR: " + nomeRecursoNoJar);
        }
        this.codigoPrograma = new BufferedReader(new InputStreamReader(inputStream));
    }

    public AnalisadorLexico(Reader leitorDeCodigo) {
        inicializarListasLexicasGlobais();
        this.codigoPrograma = new BufferedReader(leitorDeCodigo);
    }

    private void inicializarListasLexicasGlobais() {
        // Expressões Regulares
        numericos = "^\\d+(\\.\\d+)?$";
        identificadores = "^[a-zA-Z_]\\w*$";
        literais = "^\".*\"$";

        // Definição dos Tokens Especiais
        marcadores.clear();
        marcadores.add(" ");
        marcadores.add(",");
        marcadores.add(";");
        marcadores.add("(");
        marcadores.add(")");
        marcadores.add("{");
        marcadores.add("}");
        marcadores.add("\"");

        operadoresRelacionais.clear();
        operadoresRelacionais.add("=");
        operadoresRelacionais.add("<>");
        operadoresRelacionais.add("<");
        operadoresRelacionais.add(">");
        operadoresRelacionais.add("<=");
        operadoresRelacionais.add(">=");

        operadoresAritmeticos.clear();
        operadoresAritmeticos.add("+");
        operadoresAritmeticos.add("-");
        operadoresAritmeticos.add("*");
        operadoresAritmeticos.add("/");

        atribuicoes.clear();
        atribuicoes.add(":=");

        palavrasReservadas.clear();
        palavrasReservadas.add("program");
        palavrasReservadas.add("var");
        palavrasReservadas.add("begin");
        palavrasReservadas.add("end");
        palavrasReservadas.add("if");
        palavrasReservadas.add("then");
        palavrasReservadas.add("else");
        palavrasReservadas.add("while");
        palavrasReservadas.add("do");
        palavrasReservadas.add("write");
        palavrasReservadas.add("read");

        booleanos.clear();
        booleanos.add("true");
        booleanos.add("false");

        // Inicializa/reseta as listas de resultado e buffer de teste para cada nova
        // análise
        this.tokens = new ArrayList<>();
        this.codigoProgramaTeste = new StringBuilder();
    }

    public ArrayList<Token> executar() {
        // processando cada linha do arquivo/codigo de entrada
        try {
            String linha;
            while ((linha = codigoPrograma.readLine()) != null) {
                processarLinha(linha); // Seu método processarLinha
                codigoProgramaTeste.append(linha);
                codigoProgramaTeste.append(System.lineSeparator());
            }
        } catch (IOException e) { // Mais específico para readLine()
            System.err.println("Erro de I/O durante a análise léxica: " + e.getMessage());
        } catch (Exception e) { // Captura outras exceções gerais
            System.err.println("Erro geral durante a análise léxica: " + e.getMessage());
        } finally {
            if (this.codigoPrograma != null) {
                try {
                    this.codigoPrograma.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar o stream do código fonte: " + e.getMessage());
                }
            }
        }

        return this.tokens;
    }

    private void processarLinha(String linha) {
        int i = 0;
        while (i < linha.length()) {
            char character = linha.charAt(i);

            // Ignorar espaços em branco
            if (Character.isWhitespace(character)) {
                i++;
                continue;
            }

            String palavra = "";
            // Verificar se é um comentário
            if (character == '{') {
                while (i < linha.length() && linha.charAt(i) != '}') {
                    i++;
                    if (i == linha.length()) {
                        try {
                            linha = codigoPrograma.readLine();
                            if (linha != null) {
                                i = 0;
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }
                }
                i++; // Pular o '}'
                continue;
            }
            // Verificar operadores compostos como :=
            // if (i < linha.length() - 1 && character == ':' && linha.charAt(i + 1) == '=')
            // {
            // tokens.add(new Token("Atribuição", ":="));
            // i += 2;
            // continue;
            // }

            // Processar literais, identificadores, números e palavras-chave
            while (i < linha.length()) {
                // String atual = String.valueOf(linha.charAt(i));
                Character atual = linha.charAt(i);

                // if (marcadores.contains(atual)) {
                if (!Character.isLetterOrDigit(atual)) {
                    analisarToken(palavra);
                    palavra = "";

                    if (atual == '"') {
                        i++;
                        String literal = "";
                        while (i < linha.length() && linha.charAt(i) != '"') {
                            literal += linha.charAt(i);
                            i++;
                        }

                        if (i < linha.length() && linha.charAt(i) == '"') {
                            i++; // Pula a aspa final
                            tokens.add(new Token("Literal", literal));
                        } else {
                            System.out.println("ERRO: Literal não fechado corretamente.");
                        }

                        palavra = "";
                        continue;
                    } else if ((atual == '>' || atual == '<' || atual == ':') && i + 1 < linha.length()) {
                        char proximo = linha.charAt(i + 1);
                        if (proximo == '=' || (atual == '<' && proximo == '>')) {
                            palavra = atual.toString() + proximo;
                            analisarToken(palavra);
                            i++; // avança 1 extra por ser composto
                            palavra = "";
                        } else {
                            analisarToken(atual.toString());
                        }
                    }

                    else if (!Character.isWhitespace(atual)) {
                        analisarToken(atual.toString());
                    }
                } else {
                    if (!Character.isWhitespace(atual)) {
                        palavra += linha.charAt(i);
                    }
                }
                i++;
            }

            if (!palavra.equals("")) {
                analisarToken(palavra);
            }
            i++;
        }
    }

    public void analisarToken(String palavra) {
        if (palavra.isEmpty())
            return;

        if (palavrasReservadas.contains(palavra)) {
            tokens.add(new Token(palavra, "Palavra Reservada"));
        } else if (operadoresAritmeticos.contains(palavra)) {
            tokens.add(new Token(palavra, "Operador aritmetico"));
        } else if (operadoresRelacionais.contains(palavra)) {
            tokens.add(new Token(palavra,"Operador relacional"));
        } else if (atribuicoes.contains(palavra)) {
            tokens.add(new Token(palavra,"Atribuicao"));
        } else if (booleanos.contains(palavra)) {
            tokens.add(new Token(palavra,"Booleano"));
        } else if (Pattern.matches(numericos, palavra)) {
            tokens.add(new Token(palavra,"Numérico"));
        } else if (Pattern.matches(identificadores, palavra)) {
            tokens.add(new Token(palavra,"Identificador"));
        } else if (marcadores.contains(palavra)) {
            tokens.add(new Token(palavra,"Marcador"));
        } else {
            if (contidoNoAlfabeto(palavra)) {
                tokens.add(new Token(palavra,"Identificador mal formado"));
            } else {
                tokens.add(new Token(palavra,"Token inválido"));
            }
        }
    }

    private boolean contidoNoAlfabeto(String palavra) {
        String alfabeto = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_:;,.(){}+-*/<>=!\" ";

        for (char c : palavra.toCharArray()) {
            if (alfabeto.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

 

    public String getCodigoProcessado() {
        return codigoProgramaTeste.toString();
    }

    public void imprimirTabelaTokenLexema() {
        System.out.println("+------------+----------------------+");
        System.out.println("|   Token    |       Lexema         |");
        System.out.println("+------------+----------------------+");
        for (Token token : tokens) {
            System.out.printf("| %-10s | %-20s |%n",
                    token.getToken(),
                    token.getLexema());
        }
        System.out.println("+------------+----------------------+\n");
    }
}