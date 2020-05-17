package inspections;

import checks.ClassIsSynchronizedCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.AddReplaceRecordFix;

public class LombokAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {

    private final static String VALUE_ANNOTATION = "lombok.Value";
    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        aClass.getProject();
        if (aClass.hasAnnotation(VALUE_ANNOTATION) && !isSynchronizedCheck.checkMethods(aClass.getAllMethods())) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline",
                    ProblemHighlightType.INFORMATION, new AddReplaceRecordFix(aClass));
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }
}
