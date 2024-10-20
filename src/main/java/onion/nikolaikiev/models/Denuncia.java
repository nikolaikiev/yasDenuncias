package onion.nikolaikiev.models;

import java.util.HashMap;

public class Denuncia {
    private final String reportado; // Nome do jogador reportado
    private int totalDenuncias; // Contagem total de denúncias
    private String motivo; // Motivo da última denúncia
    private final HashMap<String, Integer> denunciantes; // Mapa para armazenar os jogadores que denunciaram e a contagem de denúncias

    public Denuncia(String reportado) {
        this.reportado = reportado;
        this.totalDenuncias = 0; // Inicializa a contagem de denúncias
        this.denunciantes = new HashMap<>(); // Inicializa o mapa de denunciantes
    }

    public void addDenuncia(String motivo, String denunciante) {
        this.motivo = motivo; // Atualiza o motivo da denúncia
        this.totalDenuncias++; // Incrementa a contagem total de denúncias
        this.denunciantes.put(denunciante, this.denunciantes.getOrDefault(denunciante, 0) + 1); // Atualiza a contagem para o denunciante
    }

    public int getTotalDenuncias() {
        return totalDenuncias; // Retorna a contagem total de denúncias
    }

    public String getMotivo() {
        return motivo; // Retorna o motivo da última denúncia
    }

    public String getReportado() {
        return reportado; // Retorna o nome do jogador reportado
    }

    public HashMap<String, Integer> getDenunciantes() {
        return denunciantes; // Retorna o mapa de denunciantes
    }
}
