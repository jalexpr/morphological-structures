package ru.textanalysis.tawt.ms.model.sp;

import lombok.Getter;
import lombok.Setter;
import ru.textanalysis.tawt.ms.grammeme.BearingForm;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.TypeOfSpeech;
import ru.textanalysis.tawt.ms.model.jmorfsdk.Form;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public class Word {

	protected List<Form> forms;
	protected final List<Word> mains;
	protected final List<Word> dependents;

	public Word(List<Form> forms) {
		this.forms = forms;
		this.mains = new LinkedList<>();
		this.dependents = new LinkedList<>();
	}

	public List<Form> getByFilter(Predicate<Form> predicate) {
		return forms.stream().filter(predicate).collect(Collectors.toList());
	}

	public void applyConsumer(Consumer<Form> consumer) {
		forms.forEach(consumer);
	}

	public boolean haveContainsBearingForm() {
		return forms.stream().anyMatch(form -> BearingForm.contains(form.getTypeOfSpeech()));
	}

	public boolean isOnlyOneTypeOfSpeech() {
		byte tos = forms.iterator().next().getTypeOfSpeech();
		return forms.stream().allMatch(omoForm -> omoForm.getTypeOfSpeech() == tos);
	}

	public boolean isOnlyOneForm() {
		return forms.size() == 1;
	}

	public boolean havePretext() {
		return forms.stream().anyMatch(omoForm -> omoForm.getTypeOfSpeech() == TypeOfSpeech.PRETEXT || omoForm.getTypeOfSpeech() == TypeOfSpeech.PARTICLE);
	}

	public boolean haveMain() {
		return !mains.isEmpty();
	}

	public boolean haveRelationship(Word word) {
		return mains.contains(word) || dependents.contains(word);
	}

	public void addMain(Word main) {
		mains.add(main);
	}

	public void addDependent(Word dependent) {
		dependents.add(dependent);
	}

	@Override
	public String toString() {
		return "\n\t\tWord=" + forms +
			"\n\t\t\tmain:" + mains.stream().map(Word::getForms).collect(Collectors.toList()) +
			"\n\t\t\tdependents:" + dependents.stream().map(Word::getForms).collect(Collectors.toList());
	}
}
