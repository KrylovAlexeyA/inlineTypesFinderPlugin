package checks;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;

public class ClassIsFinalCheck {

    public boolean checkClass(@NotNull PsiClass aClass) {
        return aClass.hasModifierProperty(PsiModifier.FINAL);
    }

    public boolean checkField(@NotNull PsiField field){
        return field.hasModifierProperty(PsiModifier.FINAL);
    }

    public boolean checkFields(@NotNull PsiField[] fields) {
        if (fields == null || fields.length > 0) {
            for (PsiField field : fields) {
                if (!checkField(field)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
