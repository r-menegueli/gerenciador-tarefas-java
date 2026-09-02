package br.com.darkramuza.tarefas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public final class TelaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField campoTitulo = new JTextField(28);
    private final JComboBox<Prioridade> campoPrioridade = new JComboBox<>(Prioridade.values());
    private final DefaultListModel<Tarefa> modeloLista = new DefaultListModel<>();
    private final JList<Tarefa> lista = new JList<>(modeloLista);
    private final RepositorioTarefas repositorio = new RepositorioTarefas();

    public TelaPrincipal() {
        super("Gerenciador de tarefas");
        configurarJanela();
        montarInterface();
        carregarTarefas();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);
        setMinimumSize(getSize());
    }

    private void montarInterface() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        formulario.add(new JLabel("Título"), c);
        c.gridy = 1;
        formulario.add(new JLabel("Prioridade"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoTitulo, c);
        c.gridy = 1;
        formulario.add(campoPrioridade, c);

        JButton adicionar = new JButton("Adicionar");
        JButton editar = new JButton("Salvar edição");
        JButton concluir = new JButton("Concluir/reabrir");
        JButton remover = new JButton("Remover");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(adicionar);
        botoes.add(editar);
        botoes.add(concluir);
        botoes.add(remover);

        JPanel editor = new JPanel(new BorderLayout());
        editor.add(formulario, BorderLayout.CENTER);
        editor.add(botoes, BorderLayout.SOUTH);

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(evento -> preencherFormulario());
        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setBorder(BorderFactory.createTitledBorder("Tarefas"));

        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor, rolagem);
        divisao.setResizeWeight(0.35);
        divisao.setBorder(BorderFactory.createEmptyBorder());
        add(divisao);

        adicionar.addActionListener(evento -> adicionar());
        editar.addActionListener(evento -> editar());
        concluir.addActionListener(evento -> alternarConclusao());
        remover.addActionListener(evento -> remover());
        campoTitulo.addActionListener(evento -> adicionar());
    }

    private void carregarTarefas() {
        try {
            repositorio.carregar().forEach(modeloLista::addElement);
        } catch (IOException erro) {
            mostrarErro("Não foi possível carregar as tarefas.", erro);
        }
    }

    private void adicionar() {
        try {
            Tarefa tarefa = new Tarefa(
                    campoTitulo.getText(),
                    (Prioridade) campoPrioridade.getSelectedItem());
            modeloLista.addElement(tarefa);
            persistir();
            limparFormulario();
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editar() {
        Tarefa tarefa = lista.getSelectedValue();
        if (tarefa == null) {
            avisarSelecao();
            return;
        }
        try {
            tarefa.definirTitulo(campoTitulo.getText());
            tarefa.definirPrioridade((Prioridade) campoPrioridade.getSelectedItem());
            lista.repaint();
            persistir();
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void alternarConclusao() {
        Tarefa tarefa = lista.getSelectedValue();
        if (tarefa == null) {
            avisarSelecao();
            return;
        }
        tarefa.alternarConclusao();
        lista.repaint();
        persistir();
    }

    private void remover() {
        int indice = lista.getSelectedIndex();
        if (indice < 0) {
            avisarSelecao();
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Remover a tarefa selecionada?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            modeloLista.remove(indice);
            persistir();
            limparFormulario();
        }
    }

    private void preencherFormulario() {
        Tarefa tarefa = lista.getSelectedValue();
        if (tarefa != null) {
            campoTitulo.setText(tarefa.getTitulo());
            campoPrioridade.setSelectedItem(tarefa.getPrioridade());
        }
    }

    private void limparFormulario() {
        lista.clearSelection();
        campoTitulo.setText("");
        campoPrioridade.setSelectedItem(Prioridade.MEDIA);
        campoTitulo.requestFocusInWindow();
    }

    private void persistir() {
        try {
            repositorio.salvar(Collections.list(modeloLista.elements()));
        } catch (IOException erro) {
            mostrarErro("Não foi possível salvar as tarefas.", erro);
        }
    }

    private void avisarSelecao() {
        JOptionPane.showMessageDialog(
                this,
                "Selecione uma tarefa primeiro.",
                "Nenhuma tarefa selecionada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String mensagem, Exception erro) {
        JOptionPane.showMessageDialog(
                this,
                mensagem + "\n" + erro.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}
