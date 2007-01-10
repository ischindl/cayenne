package org.apache.art;

import org.apache.art.auto._Artist;
import org.apache.cayenne.unit.util.ValidationDelegate;
import org.apache.cayenne.validation.ValidationResult;

public class Artist extends _Artist {

    protected transient ValidationDelegate validationDelegate;
    protected boolean validateForSaveCalled;

    public boolean isValidateForSaveCalled() {
        return validateForSaveCalled;
    }

    public void resetValidationFlags() {
        validateForSaveCalled = false;
    }

    public void setValidationDelegate(ValidationDelegate validationDelegate) {
        this.validationDelegate = validationDelegate;
    }

    public void validateForSave(ValidationResult validationResult) {
        validateForSaveCalled = true;
        if (validationDelegate != null) {
            validationDelegate.validateForSave(this, validationResult);
        }
        super.validateForSave(validationResult);
    }
}
