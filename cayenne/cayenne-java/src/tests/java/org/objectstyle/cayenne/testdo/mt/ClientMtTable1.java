package org.objectstyle.cayenne.testdo.mt;

import java.util.List;

import org.objectstyle.cayenne.Validating;
import org.objectstyle.cayenne.testdo.mt.auto._ClientMtTable1;
import org.objectstyle.cayenne.validation.SimpleValidationFailure;
import org.objectstyle.cayenne.validation.ValidationResult;

public class ClientMtTable1 extends _ClientMtTable1 implements Validating {
    
    protected boolean validatedForDelete;
    protected boolean validatedForInsert;
    protected boolean validatedForUpdate;
    protected boolean blow;

    // provide direct access to persistent properties for testing..

    public String getGlobalAttribute1Direct() {
        return globalAttribute1;
    }

    public String getServerAttribute1Direct() {
        return serverAttribute1;
    }

    public List getTable2ArrayDirect() {
        return table2Array;
    }
    
    public void resetValidation(boolean blow) {
        this.blow = blow;
        this.validatedForDelete = false;
        this.validatedForInsert = false;
        this.validatedForUpdate = false;
    }

    public void validateForDelete(ValidationResult validationResult) {
        validatedForDelete = true;
        
        if(blow) {
            validationResult.addFailure(new SimpleValidationFailure(this, "test error"));
        }
    }

    public void validateForInsert(ValidationResult validationResult) {
        validatedForInsert = true;
        
        if(blow) {
            validationResult.addFailure(new SimpleValidationFailure(this, "test error"));
        }
    }

    public void validateForUpdate(ValidationResult validationResult) {
        validatedForUpdate = true;
        
        if(blow) {
            validationResult.addFailure(new SimpleValidationFailure(this, "test error"));
        }
    }

    
    public boolean isValidatedForDelete() {
        return validatedForDelete;
    }

    
    public boolean isValidatedForInsert() {
        return validatedForInsert;
    }

    
    public boolean isValidatedForUpdate() {
        return validatedForUpdate;
    }
}
