package br.com.darkramuza.tarefas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public final class RepositorioTarefas {
    private final Path arquivo;

    public RepositorioTarefas() {
        this(Path.of(System.getProperty("user.home"), ".gerenciador-tarefas", "tarefas.tsv"));
    }

    public RepositorioTarefas(Path arquivo) {
        this.arquivo = arquivo.toAbsolutePath().normalize();
    }

    public List<Tarefa> carregar() throws IOException {
        List<Tarefa> tarefas = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return tarefas;
        }

        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numeroLinha = 0;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) {
                    continue;
                }
                try {
                    tarefas.add(decodificar(linha));
                } catch (RuntimeException erro) {
                    System.err.printf("Linha %d ignorada: %s%n", numeroLinha, erro.getMessage());
                }
            }
        }
        return tarefas;
    }

    public void salvar(List<Tarefa> tarefas) throws IOException {
        Path diretorio = arquivo.getParent();
        if (diretorio != null) {
            Files.createDirectories(diretorio);
        }

        Path temporario = Files.createTempFile(diretorio, "tarefas-", ".tmp");
        boolean movido = false;
        try {
            try (BufferedWriter escritor = Files.newBufferedWriter(
                    temporario, StandardCharsets.UTF_8)) {
                for (Tarefa tarefa : tarefas) {
                    escritor.write(codificar(tarefa));
                    escritor.newLine();
                }
            }

            try {
                Files.move(
                        temporario,
                        arquivo,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignorado) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
            movido = true;
        } finally {
            if (!movido) {
                Files.deleteIfExists(temporario);
            }
        }
    }

    private static String codificar(Tarefa tarefa) {
        String titulo = Base64.getUrlEncoder().withoutPadding().encodeToString(
                tarefa.getTitulo().getBytes(StandardCharsets.UTF_8));
        return String.join(
                "\t",
                tarefa.getId().toString(),
                tarefa.getPrioridade().name(),
                Boolean.toString(tarefa.isConcluida()),
                titulo);
    }

    private static Tarefa decodificar(String linha) {
        String[] partes = linha.split("\t", -1);
        if (partes.length != 4) {
            throw new IllegalArgumentException("formato inválido");
        }
        String titulo = new String(
                Base64.getUrlDecoder().decode(partes[3]), StandardCharsets.UTF_8);
        return new Tarefa(
                UUID.fromString(partes[0]),
                titulo,
                Prioridade.valueOf(partes[1]),
                Boolean.parseBoolean(partes[2]));
    }
}
