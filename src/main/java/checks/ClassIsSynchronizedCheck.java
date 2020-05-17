package checks;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.PsiModifier.SYNCHRONIZED;

public class ClassIsSynchronizedCheck {

    public boolean checkMethodsModifier(@NotNull PsiMethod method) {
        return method.hasModifierProperty(SYNCHRONIZED);
    }

    public boolean checkMethods(@NotNull PsiMethod[] methods) {
        if (methods.length > 0) {
            for (PsiMethod method : methods) {
                if ((checkMethodsModifier(method)) || (checkMethodsBody(method))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean checkMethodsBody(@NotNull PsiMethod method) {
        return method.getBody() != null &&
                (method.getBody().getText() != null &&
                        method.getBody().getText().contains(SYNCHRONIZED));
    }

}
