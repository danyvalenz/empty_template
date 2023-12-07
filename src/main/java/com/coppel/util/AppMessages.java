package com.coppel.util;

/**
 * AppMessages
 */
public class AppMessages {

    private AppMessages() {
        throw new IllegalStateException("No existe un constructor para la clase AppMessages");
    }

    public static final String INTERNAL_SERVER_ERROR = "Ocurrió un error inesperado al atender la solicitud";
    public static final String SERVER_ERROR = "Su solicitud no pudo ser procesada, intente más tarde";
    public static final String SERVER_CLIENT_ERROR = "No es posible completar la solicitud";

    public static final String DATA_TYPE_BAD_REQUEST_MESSAGE = "Alguno o varios de los datos enviados tienen formato incorrecto. Verifique que la información y el formato sean correctos.";
    public static final String ERROR_TIMEOUT = "Ocurrió un error inesperado. El servidor no dió respuesta";


}
