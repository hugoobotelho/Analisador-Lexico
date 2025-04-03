import java.io.File;

public class Principal {
    public static void main(String[] args) {
        String caminhoDoArquivo = "exemplo1.txt";
        
        // Verifica se o arquivo existe
        File arquivo = new File(caminhoDoArquivo);
        if (!arquivo.exists()) {
            System.out.println("ERRO: O arquivo não foi encontrado no caminho: " + caminhoDoArquivo);
            return;
        }

        // Se existir, executa o analisador
        AnalisadorLexico analisadorLexico = new AnalisadorLexico(caminhoDoArquivo);
        analisadorLexico.executar();
    }
}
