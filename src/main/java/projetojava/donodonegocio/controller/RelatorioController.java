package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.dto.TransacaoDTO;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.RelatorioService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/relatorio")
    public String relatorioPage() {
        return "relatorio";
    }

    @GetMapping(value = "/relatorio/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarPdf(@RequestParam("tipo") String tipo,
                                          @RequestParam(value = "status", required = false, defaultValue = "todos") String status,
                                          @RequestParam(value = "dataInicio", required = false) String dataInicio,
                                          @RequestParam(value = "dataFim", required = false) String dataFim,
                                          @RequestParam(value = "campoData", required = false, defaultValue = "auto") String campoData) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        }

        RelatorioService.TipoRelatorio tipoRelatorio = resolveTipoRelatorio(tipo);
        List<TransacaoDTO> transacoes = relatorioService.getRelatorioTransacoes(user.getEmpresaId(), tipoRelatorio);

        LocalDate ini = parseDate(dataInicio);
        LocalDate fim = parseDate(dataFim);
        transacoes = relatorioService.filtrarTransacoes(transacoes, status, ini, fim, campoData);

        byte[] pdf = gerarPdfBytes(tipoRelatorio.getNome(), status, ini, fim, campoData, transacoes);

        String filename = "relatorio-" + tipoRelatorio.name().toLowerCase() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] gerarPdfBytes(String tipoNome,
                                String status,
                                LocalDate ini,
                                LocalDate fim,
                                String campoData,
                                List<TransacaoDTO> transacoes) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Relatório - " + tipoNome, titleFont));

            String periodoTxt = buildPeriodoText(ini, fim, campoData);
            document.add(new Paragraph(periodoTxt));
            document.add(new Paragraph("Status: " + status));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.0f, 2.2f, 2.2f, 2.2f, 1.2f, 1.2f});

            addHeader(table, "ID");
            addHeader(table, "Grupo");
            addHeader(table, "Tabela");
            addHeader(table, "Responsável");
            addHeader(table, "Criada");
            addHeader(table, "Quitada");

            for (TransacaoDTO t : transacoes) {
                table.addCell(String.valueOf(t.getIdLocalEmpresa()));
                table.addCell(nullToEmpty(t.getGrupoId()));
                table.addCell(nullToEmpty(t.getTabelaResponsavel()));
                table.addCell(String.valueOf(t.getResponsavelId()));
                table.addCell(t.getDataCriacao() != null ? t.getDataCriacao().toString() : "");
                table.addCell(getResolucaoText(t));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Erro ao gerar PDF", ex);
        }
    }

    private String buildPeriodoText(LocalDate ini, LocalDate fim, String campoData) {
        if (ini != null || fim != null) {
            return "Período: " + (ini != null ? ini : "-") + " até " + (fim != null ? fim : "-") + " | CampoData: " + campoData;
        } else {
            return "Período: (sem filtro)" + " | CampoData: " + campoData;
        }
    }

    private String getResolucaoText(TransacaoDTO t) {
        if (!t.isFoiResolvido()) {
            return "-";
        }
        return t.getDataResolucao() != null ? t.getDataResolucao().toString() : "+";
    }

    private void addHeader(PdfPTable table, String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (Exception ex) {
            return null;
        }
    }

    private RelatorioService.TipoRelatorio resolveTipoRelatorio(String raw) {
        if (raw == null) {
            return RelatorioService.TipoRelatorio.TRANSFERENCIA;
        }
        for (RelatorioService.TipoRelatorio t : RelatorioService.TipoRelatorio.values()) {
            if (t.getNome().equalsIgnoreCase(raw) || t.name().equalsIgnoreCase(raw)) {
                return t;
            }
        }
        return RelatorioService.TipoRelatorio.TRANSFERENCIA;
    }

    private CustomUserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud) {
            return cud;
        }
        return null;
    }
}
