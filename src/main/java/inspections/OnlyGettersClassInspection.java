package inspections;

import checks.MethodsOfObjectCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnlyGettersClassInspection extends AbstractBaseJavaLocalInspectionTool {

    private MethodsOfObjectCheck inheritedFromObjectCheck = new MethodsOfObjectCheck();

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (checkMethods(aClass.getAllMethods())) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline", ProblemHighlightType.INFORMATION);
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private boolean checkMethods(PsiMethod[] methods) {
        if (methods.length == 0) return false;
        for (PsiMethod method : methods) {
            if (!method.getName().startsWith("get") && !method.isConstructor()) {
                if (inheritedFromObjectCheck.checkMethods(method)) continue;
                return false;
            }
        }
        return true;
    }
}
