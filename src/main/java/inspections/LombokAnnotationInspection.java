package inspections;

import checks.ClassIsSynchronizedCheck;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    class AddReplaceRecordFix extends LocalQuickFixOnPsiElement {
        private final String FAMILY_NAME = "Replace this class with a record";
        private PsiClass aClass;

        protected AddReplaceRecordFix(@NotNull PsiClass aClass) {
            super(aClass);
            this.aClass = aClass;
        }

        @Override
        public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getText() {
            return FAMILY_NAME;
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
            PsiClass record = PsiElementFactory.getInstance(project).createRecord(aClass.getName());
            PsiRecordHeader recordHeader = PsiElementFactory.getInstance(project).createRecordHeaderFromText("String name", aClass);
            record.getRecordHeader().replace(recordHeader);
            aClass.replace(record);
        }

        @Override
        public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
            return FAMILY_NAME;
        }
    }
}
