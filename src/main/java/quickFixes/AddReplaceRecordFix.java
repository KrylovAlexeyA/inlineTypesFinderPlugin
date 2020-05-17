package quickFixes;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddReplaceRecordFix extends LocalQuickFixOnPsiElement {
    private final String FAMILY_NAME = "Replace this class with a record";
    private PsiClass aClass;

    public AddReplaceRecordFix(@NotNull PsiClass aClass) {
        super(aClass);
        this.aClass = aClass;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getText() {
        return FAMILY_NAME;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        String stringFields = concatenateFields(aClass.getFields());
        PsiClass record = PsiElementFactory.getInstance(project).createRecord(aClass.getName());
        PsiRecordHeader recordHeader = PsiElementFactory.getInstance(project).createRecordHeaderFromText(stringFields, aClass);
        record.getRecordHeader().replace(recordHeader);
        aClass.replace(record);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
        return FAMILY_NAME;
    }

    private String concatenateFields(PsiField[] psiFields) {
        StringBuilder builder = new StringBuilder();
        int count = psiFields.length;
        for (PsiField field : psiFields) {
            String type = field.getType().toString().substring(field.getType().toString().indexOf(":") + 1);
            builder.append(type);
            builder.append(" ");
            builder.append(field.getName());
            count--;
            if (count > 0) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}