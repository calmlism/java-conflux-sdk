package org.cfx.protocol.core.methods.response;

import org.cfx.protocol.core.Response;

public class CfxSubmitHashrate extends Response<Boolean> {

    public boolean submissionSuccessful() {
        return getResult();
    }
}
