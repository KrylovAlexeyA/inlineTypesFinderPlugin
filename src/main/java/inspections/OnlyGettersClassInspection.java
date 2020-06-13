package inspections;

import checks.ClassIsSynchronizedCheck;
import checks.MethodsOfObjectCheck;
import checks.NoModifiedFieldsCheck;
import com.intellij.codeInspection.*;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.ReplaceWithInlineFix;
import quickFixes.ReplaceWithRecordFix;

public class OnlyGettersClassInspection extends AbstractBaseJavaLocalInspectionTool {

    private MethodsOfObjectCheck inheritedFromObjectCheck = new MethodsOfObjectCheck();
    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private NoModifiedFieldsCheck noModifiedFieldsCheck = new NoModifiedFieldsCheck();

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (checkMethods(aClass.getAllMethods()) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods()) &&
                noModifiedFieldsCheck.checkClass(aClass) &&
                !isSynchronizedCheck.isBlockedBySynchronized(aClass)) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline",
                    ProblemHighlightType.INFORMATION,
                    new ReplaceWithRecordFix(aClass), new ReplaceWithInlineFix(aClass));
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }

    private boolean checkMethods(PsiMethod[] methods) {
        if (methods.length == 0) return false;
        long i = 0;
        for (PsiMethod method : methods) {
            boolean startsWithGet = false;
            if (method.getName().startsWith("get")) {
                startsWithGet = true;
                i++;
            }
            if (!startsWithGet && !method.isConstructor()) {
                if (inheritedFromObjectCheck.checkMethod(method) || method.hasModifier(JvmModifier.NATIVE)) {
                    continue;
                }
                return false;
            }
        }
        return i > 1;
    }
}
