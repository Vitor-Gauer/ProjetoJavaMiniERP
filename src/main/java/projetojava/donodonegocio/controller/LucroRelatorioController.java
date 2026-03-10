package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.dto.LucroRelatorioDTO;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.LucroRelatorioService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/relatorio/lucro")
@RequiredArgsConstructor
public class LucroRelatorioController {
    
    private final LucroRelatorioService lucroRelatorioService;
    
    @GetMapping
    public String lucroPage() {
        return "relatorios/lucro";
    }
    
    @GetMapping("/gerar")
    public String gerarRelatorio(@RequestParam int anoInicial,
                                @RequestParam int anoFinal,
                                @RequestParam(required = false) String meses,
                                Model model) {
        try {
            CustomUserDetails user = getUserDetails();
            if (user == null) {
                return "redirect:/login";
            }
            
            List<Integer> mesesList = parseMeses(meses);
            List<LucroRelatorioDTO> relatorio = lucroRelatorioService.gerarRelatorioLucro(
                    user.getEmpresaId(), anoInicial, anoFinal, mesesList
            );
            
            model.addAttribute("relatorio", relatorio);
            model.addAttribute("anoInicial", anoInicial);
            model.addAttribute("anoFinal", anoFinal);
            model.addAttribute("meses", mesesList);
            
            return "relatorios/lucro-resultado";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao gerar relatório: " + e.getMessage());
            return "relatorios/erro";
        }
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> gerarPDF(@RequestParam int anoInicial,
                                         @RequestParam int anoFinal,
                                         @RequestParam(required = false) String meses) {
        try {
            CustomUserDetails user = getUserDetails();
            if (user == null) {
                return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
            }
            
            List<Integer> mesesList = parseMeses(meses);
            List<LucroRelatorioDTO> relatorio = lucroRelatorioService.gerarRelatorioLucro(
                    user.getEmpresaId(), anoInicial, anoFinal, mesesList
            );
            
            byte[] pdf = gerarPDFBytes(relatorio, anoInicial, anoFinal, mesesList);
            
            String filename = String.format("relatorio-lucro-%d-%d.pdf", anoInicial, anoFinal);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private byte[] gerarPDFBytes(List<LucroRelatorioDTO> relatorio, int anoInicial, int anoFinal, List<Integer> meses) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        
        document.add(new Paragraph("Relatório de Lucro por Produto", titleFont));
        document.add(new Paragraph("Período: " + anoInicial + " - " + anoFinal));
        document.add(new Paragraph("Meses: " + formatarMeses(meses)));
        document.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15f, 8f, 8f, 12f, 12f, 10f, 15f, 20f});
        
        addHeader(table, "Produto", headerFont);
        addHeader(table, "Qtd. Vendas", headerFont);
        addHeader(table, "Qtd. Compras", headerFont);
        addHeader(table, "Lucro Atual", headerFont);
        addHeader(table, "Lucro Passado", headerFont);
        addHeader(table, "Crescimento %", headerFont);
        addHeader(table, "Liquidez", headerFont);
        addHeader(table, "Análise Mensal", headerFont);
        
        for (LucroRelatorioDTO dto : relatorio) {
            table.addCell(new Phrase(dto.getProdutoNome(), normalFont));
            table.addCell(new Phrase(String.valueOf(dto.getQuantidadeVendas()), normalFont));
            table.addCell(new Phrase(String.valueOf(dto.getQuantidadeCompras()), normalFont));
            table.addCell(new Phrase(formatarCurrency(dto.getLucroAtual()), normalFont));
            table.addCell(new Phrase(formatarCurrency(dto.getLucroPassado()), normalFont));
            table.addCell(new Phrase(formatarPercentual(dto.getCrescimentoAnual()), normalFont));
            table.addCell(new Phrase(calcularLiquidez(dto), normalFont));
            table.addCell(new Phrase(formatarAnaliseMensal(dto.getLucroMensal()), normalFont));
        }
        
        document.add(table);
        document.close();
        return out.toByteArray();
    }
    
    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5f);
        cell.setBackgroundColor(new Color(220, 220, 220));
        table.addCell(cell);
    }
    
    private String formatarCurrency(BigDecimal value) {
        return "R$ " + (value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
    }
    
    private String formatarPercentual(BigDecimal value) {
        return (value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO) + "%";
    }
    
    private String calcularLiquidez(LucroRelatorioDTO dto) {
        if (dto.getQuantidadeVendas() == 0) return "N/A";
        return String.format("%.1f", (double) dto.getQuantidadeCompras() / dto.getQuantidadeVendas());
    }
    
    private String formatarAnaliseMensal(List<LucroRelatorioDTO.MesLucroDTO> lucroMensal) {
        if (lucroMensal == null || lucroMensal.isEmpty()) return "N/A";
        
        long positivos = lucroMensal.stream()
                .mapToLong(m -> m.getLucro().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0)
                .sum();
        
        return String.format("%d/%d meses positivos", positivos, lucroMensal.size());
    }
    
    private List<Integer> parseMeses(String meses) {
        if (meses == null || meses.trim().isEmpty()) {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        }
        
        return Arrays.stream(meses.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .sorted()
                .toList();
    }
    
    private String formatarMeses(List<Integer> meses) {
        return meses.stream()
                .map(m -> LocalDate.of(2024, m, 1).getMonth().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Todos");
    }
    
    private CustomUserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        
        Object principal = auth.getPrincipal();
        return principal instanceof CustomUserDetails ? (CustomUserDetails) principal : null;
    }
}
