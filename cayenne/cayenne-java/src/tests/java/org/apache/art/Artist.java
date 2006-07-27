package org.apache.art;

import org.apache.art.auto._Artist;
import org.apache.cayenne.validation.ValidationResult;

public class Artist extends _Artist {

    protected boolean validateForSaveCalled;

    public boolean isValidateForSaveCalled() {
        return validateForSaveCalled;
    }

    public void resetValidationFlags() {
        validateForSaveCalled = false;
    }

    public void validateForSave(ValidationResult validationResult) {
        validateForSaveCalled = true;
        super.validateForSave(validationResult);
    }
}
