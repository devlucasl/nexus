package br.nexus.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = true;
}