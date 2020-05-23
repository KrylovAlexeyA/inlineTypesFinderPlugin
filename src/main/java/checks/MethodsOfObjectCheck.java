package checks;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodsOfObjectCheck {
    private final static Set<String> objectMethods = Set.of(
            "getClass",
            "hashCode",
            "equals",
            "clone",
            "toString",
            "notify",
            "notifyAll",
            "wait",
            "finalize");

    public boolean checkMethod(@NotNull PsiMethod method) {
        return objectMethods.contains(method.getName());
    }
}
