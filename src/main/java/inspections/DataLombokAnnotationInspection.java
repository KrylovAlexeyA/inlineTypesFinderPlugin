package inspections;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataLombokAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {

    private static String DATA_ANNOTATION = "@Data";

    @Nullable
    @Override
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        PsiAnnotation annotation = aClass.getAnnotation(DATA_ANNOTATION);
        if (annotation != null) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline", ProblemHighlightType.INFORMATION);
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }
}
