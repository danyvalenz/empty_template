package com.coppel.views;

public class MetaView {

    //External View for User
    public interface External {
    }
    //Intenal View for User, will inherit all filds in External
    public interface Internal extends External {
    }

}
