package br.com.darkramuza.tarefas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Aplicacao {
    private Aplicacao() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignorado) {
            // Mantém o tema padrão quando o tema do sistema não estiver disponível.
        }
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
