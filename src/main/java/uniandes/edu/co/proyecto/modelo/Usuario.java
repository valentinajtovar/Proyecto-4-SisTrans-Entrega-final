package uniandes.edu.co.proyecto.modelo;




@ToString
public class Usuario {
    
    private Integer idUsuario;

    private TipoDocumento tipoDocumento;

    private Integer numeroDocumento;

    private String nombre;

    private String nacionalidad;

    private String direccionFisica;

    private String direccionDigital;

    private Long telefono;

    private Integer codigoPostal;

    private String login;

    private String clave;

    @ManyToOne
    @JoinColumn(name ="tipo_usuario", referencedColumnName = "tipoUsuario")
    private TipoUsuario tipoUsuario;
 

    public Usuario(){;}

    
    public Usuario(Integer idUsuario,TipoDocumento tipoDocumento, Integer numeroDocumento, String nombre,
            String nacionalidad, String direccionFisica, String direccionDigital, Long telefono,
            Integer codigoPostal, TipoUsuario tipoUsuario, String login, String clave) {
        this.idUsuario=idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.direccionFisica = direccionFisica;
        this.direccionDigital = direccionDigital;
        this.telefono = telefono;
        this.codigoPostal = codigoPostal;
        this.tipoUsuario = tipoUsuario;
        this.login = login;
        this.clave = clave;
    }

    public Integer getId() {
        return idUsuario;
    }

    public void setId(Integer id) {
        this.idUsuario = id;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getDireccionFisica() {
        return direccionFisica;
    }

    public void setDireccionFisica(String direccionFisica) {
        this.direccionFisica = direccionFisica;
    }

    public String getDireccionDigital() {
        return direccionDigital;
    }

    public void setDireccionDigital(String direccionDigital) {
        this.direccionDigital = direccionDigital;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getLogin() {
        return login;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

}
