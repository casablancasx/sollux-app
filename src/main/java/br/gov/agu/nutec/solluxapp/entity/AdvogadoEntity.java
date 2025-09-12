package br.gov.agu.nutec.solluxapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}
