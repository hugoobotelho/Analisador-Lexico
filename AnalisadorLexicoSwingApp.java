
// AnalisadorLexicoSwingApp.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;

public class AnalisadorLexicoSwingApp extends JFrame {

  private JComboBox<String> arquivoComboBox;
  private JTextArea codigoTextArea;
  private JButton analisarButton;
  private JTable resultadoTable;
  private DefaultTableModel tableModel;
  private JRadioButton radioEscolherArquivo, radioDigitarCodigo;
  private ButtonGroup modoEntradaGrupo;
  private final String[] arquivosExemplo = { "exemplo1.txt", "exemplo2.txt", "exemplo3.txt", "exemplo4.txt",
      "exemplo5.txt" };

  public AnalisadorLexicoSwingApp() {
    super("Analisador Léxico");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 700);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(10, 10));
    inicializarComponentesUISwing();
    configurarLayoutUISwing();
    adicionarListenersUISwing();
    ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
  }

  private void inicializarComponentesUISwing() {
    radioEscolherArquivo = new JRadioButton("Analisar arquivo de exemplo", true);
    radioDigitarCodigo = new JRadioButton("Digitar/Colar código");
    modoEntradaGrupo = new ButtonGroup();
    modoEntradaGrupo.add(radioEscolherArquivo);
    modoEntradaGrupo.add(radioDigitarCodigo);

    arquivoComboBox = new JComboBox<>(arquivosExemplo);
    codigoTextArea = new JTextArea(10, 70);
    codigoTextArea.setLineWrap(true);
    codigoTextArea.setWrapStyleWord(true);
    codigoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
    codigoTextArea.setEnabled(false);

    analisarButton = new JButton("Analisar Código");

    String[] nomeColunas = { "Token", "Lexema" };
    tableModel = new DefaultTableModel(nomeColunas, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    resultadoTable = new JTable(tableModel);
    resultadoTable.setFillsViewportHeight(true);
    resultadoTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
    resultadoTable.setRowHeight(20);
  }

  private void configurarLayoutUISwing() {
    JPanel painelEntradaPrincipal = new JPanel();
    painelEntradaPrincipal.setLayout(new BoxLayout(painelEntradaPrincipal, BoxLayout.Y_AXIS));
    painelEntradaPrincipal.setBorder(BorderFactory.createTitledBorder("Opções de Entrada"));

    JPanel painelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
    painelRadio.add(radioEscolherArquivo);
    painelRadio.add(radioDigitarCodigo);
    painelEntradaPrincipal.add(painelRadio);

    JPanel painelArquivoCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    painelArquivoCombo.add(arquivoComboBox);
    painelEntradaPrincipal.add(painelArquivoCombo);

    painelEntradaPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));

    // ***** MODIFICAÇÃO PARA CENTRALIZAR O LABEL *****
    JLabel labelCodigoAnalise = new JLabel("Digite abaixo o código para análise:");
    // labelCodigoAnalise.setFont(new Font("SansSerif", Font.BOLD, 12)); //
    // Opcional: para dar destaque

    JPanel painelLabelCodigo = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Usa FlowLayout.CENTER
    painelLabelCodigo.add(labelCodigoAnalise);
    painelEntradaPrincipal.add(painelLabelCodigo); // Adiciona o painel com o label centralizado
    // *************************************************

    JScrollPane scrollTextArea = new JScrollPane(codigoTextArea);
    // Para garantir que o JScrollPane não se estique demais verticalmente dentro do
    // BoxLayout
    // e que o JTextArea tenha um tamanho preferencial respeitado pelo scrollpane:
    // Dimension textAreaSize = new Dimension(400, 200); // Exemplo de tamanho
    // codigoTextArea.setPreferredScrollableViewportSize(textAreaSize); // Se o
    // JTextArea for muito pequeno
    // scrollTextArea.setPreferredSize(textAreaSize); // Define o tamanho preferido
    // do ScrollPane
    // scrollTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE,
    // textAreaSize.height + 20)); // Limita altura máxima

    painelEntradaPrincipal.add(scrollTextArea);

    painelEntradaPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
    JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
    painelBotao.add(analisarButton);
    painelEntradaPrincipal.add(painelBotao);

    add(painelEntradaPrincipal, BorderLayout.NORTH);

    JPanel painelSaidaTabela = new JPanel(new BorderLayout());
    painelSaidaTabela.setBorder(BorderFactory.createTitledBorder("Resultado da Análise Léxica"));
    painelSaidaTabela.add(new JScrollPane(resultadoTable), BorderLayout.CENTER);
    add(painelSaidaTabela, BorderLayout.CENTER);
  }

  private void adicionarListenersUISwing() {
    analisarButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispararAnaliseLexica();
      }
    });

    ActionListener listenerModo = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean isArquivoSelecionado = radioEscolherArquivo.isSelected();
        arquivoComboBox.setEnabled(isArquivoSelecionado);
        codigoTextArea.setEnabled(!isArquivoSelecionado);
      }
    };
    radioEscolherArquivo.addActionListener(listenerModo);
    radioDigitarCodigo.addActionListener(listenerModo);
    arquivoComboBox.setEnabled(true);
    codigoTextArea.setEnabled(false);
  }

  private void dispararAnaliseLexica() {
    tableModel.setRowCount(0);
    AnalisadorLexico analisador;
    ArrayList<Token> resultados;
    ArrayList<String> tokensMalFormados;
    ArrayList<String> tokensInvalidos;

    try {
      if (radioEscolherArquivo.isSelected()) {
        String arquivoSelecionado = (String) arquivoComboBox.getSelectedItem();
        if (arquivoSelecionado == null) {
          JOptionPane.showMessageDialog(this, "Selecione um arquivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
          return;
        }
        analisador = new AnalisadorLexico(arquivoSelecionado);
      } else {
        String codigo = codigoTextArea.getText();
        if (codigo.trim().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Digite ou cole um código para analisar.", "Aviso",
              JOptionPane.WARNING_MESSAGE);
          return;
        }
        analisador = new AnalisadorLexico(new StringReader(codigo));
      }

      resultados = analisador.executar();
      tokensMalFormados = analisador.getTokensMalFormados();
      tokensInvalidos = analisador.getTokensInvalidos();

      if (resultados != null) {
        for (Token token : resultados) {
          tableModel.addRow(new Object[] { token.getToken(), token.getLexema() });
        }
      }

      if (!tokensMalFormados.isEmpty() || !tokensInvalidos.isEmpty()) {
        StringBuilder sb = new StringBuilder("Tokens com problemas:\n");

        if (!tokensMalFormados.isEmpty()) {
          sb.append("\nTokens mal formados:\n");
          for (String t : tokensMalFormados) {
            sb.append("- ").append(t).append("\n");
          }
        }

        if (!tokensInvalidos.isEmpty()) {
          sb.append("\nTokens inválidos:\n");
          for (String t : tokensInvalidos) {
            sb.append("- ").append(t).append("\n");
          }
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Aviso", JOptionPane.WARNING_MESSAGE);
      } else if (resultados != null && resultados.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nenhum token reconhecido.", "Análise Concluída",
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Análise léxica finalizada com sucesso!", "Análise Concluída",
            JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (RuntimeException re) {
      JOptionPane.showMessageDialog(this, "Erro ao analisar: " + re.getMessage(), "Erro de Análise",
          JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro Crítico",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
    }
    try {
      UIManager.put("ScrollBar.width", 5); // Mantenha esta linha se quiser forçar a largura
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new AnalisadorLexicoSwingApp().setVisible(true);
      }
    });
  }
}