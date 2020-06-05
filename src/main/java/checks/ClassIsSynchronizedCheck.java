package checks;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
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

    public boolean isBlockedBySynchronized(@NotNull PsiClass psiClass) {
        Query<PsiReference> references = ReferencesSearch.search(psiClass);
        if (!references.findAll().isEmpty()) {
            for (PsiReference reference : references) {
                PsiElement element = reference.getElement().getContext().getParent();
                if (element instanceof PsiVariable) {
                    System.out.println(element.toString());
                    Query<PsiReference> variableRefs = ReferencesSearch.search(element);
                    System.out.println(variableRefs.toString());
                    for (PsiReference variableRef : variableRefs) {
                        if (variableRef.getElement().getParent() instanceof PsiSynchronizedStatement) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
