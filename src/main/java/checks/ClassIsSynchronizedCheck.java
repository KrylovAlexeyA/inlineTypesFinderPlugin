package checks;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.PsiModifier.SYNCHRONIZED;

public class ClassIsSynchronizedCheck {

    public boolean checkMethod(@NotNull PsiMethod method) {
        return method.hasModifierProperty(SYNCHRONIZED);
    }

    public boolean checkMethods(@NotNull PsiMethod[] methods) {
        if (methods.length > 0) {
            for (PsiMethod method : methods) {
                if (method.hasModifierProperty(SYNCHRONIZED)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
