package br.gov.agu.nutec.solluxapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_advogados")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdvogadoEntity {

    @Id
    @Column(name = "advogado_id")
    private Long advogadoId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "is_suspeito", nullable = false)
    private boolean isSuspeito;
}
