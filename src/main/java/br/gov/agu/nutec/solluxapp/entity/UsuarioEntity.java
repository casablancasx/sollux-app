package br.gov.agu.nutec.solluxapp.entity;

import br.gov.agu.nutec.solluxapp.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @Column(name = "sapiens_id")
    private Long sapiensId;

    private String nome;

    private String email;

    private String cpf;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<PlanilhaEntity> planilhas;


}
