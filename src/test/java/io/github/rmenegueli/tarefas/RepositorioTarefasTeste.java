package io.github.rmenegueli.tarefas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class RepositorioTarefasTeste {
    private RepositorioTarefasTeste() {
    }

    public static void main(String[] args) throws Exception {
        Path diretorio = Files.createTempDirectory("teste-tarefas-");
        Path arquivo = diretorio.resolve("tarefas.tsv");
        try {
            RepositorioTarefas repositorio = new RepositorioTarefas(arquivo);
            Tarefa primeira = new Tarefa("Revisar; relatório", Prioridade.ALTA);
            primeira.alternarConclusao();
            Tarefa segunda = new Tarefa("Simular circuito", Prioridade.MEDIA);

            repositorio.salvar(List.of(primeira, segunda));
            List<Tarefa> carregadas = repositorio.carregar();

            if (carregadas.size() != 2) {
                throw new AssertionError("A quantidade de tarefas restauradas deveria ser 2.");
            }
            if (!carregadas.get(0).getTitulo().equals("Revisar; relatório")) {
                throw new AssertionError("O título com caracteres especiais não foi restaurado.");
            }
            if (!carregadas.get(0).isConcluida()) {
                throw new AssertionError("O estado de conclusão não foi restaurado.");
            }
            if (carregadas.get(1).getPrioridade() != Prioridade.MEDIA) {
                throw new AssertionError("A prioridade da segunda tarefa não foi restaurada.");
            }
            System.out.println("Teste de persistência aprovado.");
        } finally {
            Files.deleteIfExists(arquivo);
            Files.deleteIfExists(diretorio);
        }
    }
}
