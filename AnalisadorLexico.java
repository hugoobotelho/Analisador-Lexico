import java.io.*;
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

    public AnalisadorLexico(String caminhoArquivo) {
        try {
            // Expressões Regulares para Números, Identificadores e Strings
            numericos = "^\\d+(\\.\\d+)?$";
            identificadores = "^[a-zA-Z_]\\w*$";
            literais = "^\".*\"$";

            // Definição dos Tokens Especiais
            marcadores.add(" ");
            marcadores.add(",");
            marcadores.add(";");
            marcadores.add("(");
            marcadores.add(")");
            marcadores.add("{");
            marcadores.add("}");
            marcadores.add("\""); // <- isso mesmo, a aspa dupla como marcador

            operadoresRelacionais.add("=");
            operadoresRelacionais.add("<>");
            operadoresRelacionais.add("<");
            operadoresRelacionais.add(">");
            operadoresRelacionais.add("<=");
            operadoresRelacionais.add(">=");

            operadoresAritmeticos.add("+");
            operadoresAritmeticos.add("-");
            operadoresAritmeticos.add("*");
            operadoresAritmeticos.add("/");

            atribuicoes.add(":=");

            palavrasReservadas.add("program");
            palavrasReservadas.add("var");
            palavrasReservadas.add("begin");
            palavrasReservadas.add("end");
            palavrasReservadas.add("if");
            palavrasReservadas.add("then");
            palavrasReservadas.add("else");
            palavrasReservadas.add("while");
            palavrasReservadas.add("do");

            booleanos.add("true");
            booleanos.add("false");

            codigoPrograma = new BufferedReader(new FileReader(caminhoArquivo));
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado!");
        }
    }

    public void executar() {
        System.out.println("Executando a análise léxica...");
        try {
            String linha;
            while ((linha = codigoPrograma.readLine()) != null) {
                processarLinha(linha);
            }
            System.out.println("Analise lexica concluida!");
            imprimirTabelaTokenLexema();
        } catch (Exception e) {
            System.out.println("Erro durante a análise léxica: " + e.getMessage());
        }
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
                    if (i == linha.length()) { // se chegou no final da linha e nao achou o } entao passa para a proxima
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
                String atual = String.valueOf(linha.charAt(i));

                if (atual.equals("\"")) {
                    i++; // Pula a primeira aspa
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
                }

                if (marcadores.contains(atual)) {
                    analisarToken(palavra);
                    palavra = "";
                    if (!atual.equals(" ")) {
                        analisarToken(atual);
                    }
                } else {
                    palavra += linha.charAt(i);
                }
                i++;

                // System.out.println("Atualizou o i e : " + linha.length() + " e a palvra é: "
                // + palavra);
            }

            if (!palavra.equals("")) {
                analisarToken(palavra);
            }
            i++;
        }
    }

    public void analisarToken(String palavra) {
        // System.out.println("A PALAVRA E: " + palavra);
        if (palavra.isEmpty())
            return;

        if (palavrasReservadas.contains(palavra)) {
            tokens.add(new Token("Palavra Reservada", palavra));
        } else if (operadoresAritmeticos.contains(palavra)) {
            tokens.add(new Token("Operador aritmetico", palavra));
        } else if (operadoresRelacionais.contains(palavra)) {
            tokens.add(new Token("Operador relacional", palavra));
        } else if (atribuicoes.contains(palavra)) {
            tokens.add(new Token("Atribuicao", palavra));
        } else if (booleanos.contains(palavra)) {
            tokens.add(new Token("Booleano", palavra));
        } else if (Pattern.matches(numericos, palavra)) {
            tokens.add(new Token("Numérico", palavra));
        } else if (Pattern.matches(identificadores, palavra)) {
            tokens.add(new Token("Identificador", palavra));
        } else if (marcadores.contains(palavra)) {
            tokens.add(new Token("Marcador", palavra));
        } else {
            System.out.println("ERRO: Token não reconhecido: " + palavra);
        }
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
        System.out.println("+------------+----------------------+");
    }
}
