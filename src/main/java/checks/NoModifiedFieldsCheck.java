package checks;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class NoModifiedFieldsCheck {
    private final static String COMMENTS = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
    private MethodsOfObjectCheck methodsOfObjectCheck = new MethodsOfObjectCheck();

    public boolean checkClass(@NotNull PsiClass aClass) {
        for (PsiField field : aClass.getFields()) {
            for (PsiMethod method : aClass.getMethods()) {
                if (methodsOfObjectCheck.checkMethod(method) || method.isConstructor()) {
                    continue;
                }
                String methodText = method.getText().replaceAll(COMMENTS,"");
                if (methodText.contains(field.getName() + " =")) {
                    return false;
                }
            }
        }
        return true;
    }
}
