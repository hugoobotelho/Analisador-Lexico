import java.io.File;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String caminhoDoArquivo = "";
        Boolean flag = true;

        while ( flag ) {
            
        System.out.println("=== ANALISADOR LÉXICO ===");
        System.out.println("Escolha o número do programa para executar:");
        System.out.println("1 - exemplo1.txt");
        System.out.println("2 - exemplo2.txt");
        System.out.println("3 - exemplo3.txt");
        System.out.println("4 - exemplo4.txt");
        System.out.println("5 - exemplo5.txt");
        System.out.println("6 - sair");
        System.out.print("Digite sua opção (1-6): ");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (opcao) {
            case 1:
                caminhoDoArquivo = "exemplo1.txt";
                break;
            case 2:
                caminhoDoArquivo = "exemplo2.txt";
                break;
            case 3:
                caminhoDoArquivo = "exemplo3.txt";
                break;
            case 4:
                caminhoDoArquivo = "exemplo4.txt";
                break;
            case 5:
                caminhoDoArquivo = "exemplo5.txt";
                break;
            case 6:
                flag = false;
                break;
            default:
                System.out.println("Opção inválida. Encerrando o programa.");
                return;
        }

        // Verifica se o arquivo existe
        File arquivo = new File(caminhoDoArquivo);
        if (!arquivo.exists()) {
            System.out.println("ERRO: O arquivo não foi encontrado no caminho: " + caminhoDoArquivo);
            return;
        }

        // Executa o analisador
        AnalisadorLexico analisadorLexico = new AnalisadorLexico(caminhoDoArquivo);
        analisadorLexico.executar();
        }
    }
}
