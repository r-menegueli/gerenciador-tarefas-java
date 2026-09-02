package io.github.rmenegueli.tarefas;

import java.util.Objects;
import java.util.UUID;

public final class Tarefa {
    private final UUID id;
    private String titulo;
    private Prioridade prioridade;
    private boolean concluida;

    public Tarefa(String titulo, Prioridade prioridade) {
        this(UUID.randomUUID(), titulo, prioridade, false);
    }

    Tarefa(UUID id, String titulo, Prioridade prioridade, boolean concluida) {
        this.id = Objects.requireNonNull(id, "id");
        definirTitulo(titulo);
        this.prioridade = Objects.requireNonNull(prioridade, "prioridade");
        this.concluida = concluida;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void definirTitulo(String novoTitulo) {
        String normalizado = Objects.requireNonNull(novoTitulo, "titulo").trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("O título não pode ficar vazio.");
        }
        this.titulo = normalizado;
    }

    public void definirPrioridade(Prioridade novaPrioridade) {
        this.prioridade = Objects.requireNonNull(novaPrioridade, "prioridade");
    }

    public void alternarConclusao() {
        concluida = !concluida;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s", concluida ? "✓" : "○", prioridade, titulo);
    }
}
