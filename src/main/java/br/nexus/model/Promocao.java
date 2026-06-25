package br.nexus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "promocoes")
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto", nullable = false, length = 30)
    private TipoDesconto tipoDesconto;

    @Column(name = "valor_desconto", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private boolean ativa = true;

    public Promocao() {
    }

    public Promocao(
            Produto produto,
            TipoDesconto tipoDesconto,
            BigDecimal valorDesconto,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        this.produto = produto;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = normalizar(valorDesconto);
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativa = true;
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = normalizar(valorDesconto);
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public boolean estaVigente(LocalDate data) {
        return ativa && !data.isBefore(dataInicio) && !data.isAfter(dataFim);
    }

    public BigDecimal calcularDescontoUnitario(BigDecimal precoBase) {
        BigDecimal desconto;

        if (TipoDesconto.PERCENTUAL.equals(tipoDesconto)) {
            desconto = precoBase
                    .multiply(valorDesconto)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            desconto = valorDesconto;
        }

        if (desconto.compareTo(precoBase) > 0) {
            return precoBase.setScale(2, RoundingMode.HALF_UP);
        }

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }

    public String statusAtual(LocalDate data) {
        if (!ativa) {
            return "INATIVA";
        }

        if (data.isBefore(dataInicio)) {
            return "PROGRAMADA";
        }

        if (data.isAfter(dataFim)) {
            return "ENCERRADA";
        }

        return "ATIVA";
    }

    private BigDecimal normalizar(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}