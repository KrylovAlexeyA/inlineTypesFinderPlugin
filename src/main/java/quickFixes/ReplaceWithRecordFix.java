package quickFixes;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReplaceWithRecordFix extends LocalQuickFixOnPsiElement {
    private final String FAMILY_NAME = "Replace this class with a record";
    private PsiClass aClass;

    public ReplaceWithRecordFix(@NotNull PsiClass aClass) {
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
        PsiFile containingFile = aClass.getContainingFile();
        PsiClass record = PsiElementFactory.getInstance(project).createRecord(aClass.getName());
        PsiRecordHeader recordHeader = PsiElementFactory.getInstance(project).createRecordHeaderFromText(stringFields, aClass);
        WriteCommandAction.runWriteCommandAction(project, FAMILY_NAME, null, () -> {
            record.getRecordHeader().replace(recordHeader);
            aClass.replace(record);
            IntentionAction optimizeImportsFix = QuickFixFactory.getInstance().createOptimizeImportsFix(false);
            if (optimizeImportsFix.isAvailable(project, null, file)) {
                optimizeImportsFix.invoke(project, null, file);
            }

        }, containingFile);
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