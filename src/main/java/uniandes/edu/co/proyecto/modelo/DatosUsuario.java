package uniandes.edu.co.proyecto.modelo;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "DATOS_USUARIO")
public class DatosUsuario {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDatosUsuario;

    private String login;

    private String clave;


    @OneToOne(mappedBy = "datosUsuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Usuario usuario;


    public DatosUsuario(){;}

    public DatosUsuario(String login, String clave) {
        this.login = login;
        this.clave = clave;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Integer getIdDatosUsuario() {
        return idDatosUsuario;
    }

    public void setIdDatosUsuario(Integer idDatosUsuario) {
        this.idDatosUsuario = idDatosUsuario;
    }

    




    



}
