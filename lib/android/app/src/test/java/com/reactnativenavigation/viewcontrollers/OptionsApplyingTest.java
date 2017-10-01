package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.TestContainerView;
import com.reactnativenavigation.parse.NavigationOptions;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OptionsApplyingTest extends BaseTest {
	private Activity activity;
	private ContainerViewController uut;
	private ContainerViewController.ContainerView view;
	private NavigationOptions initialNavigationOptions;

	@Override
	public void beforeEach() {
		super.beforeEach();
		activity = newActivity();
		initialNavigationOptions = new NavigationOptions();
		view = spy(new TestContainerView(activity));
		uut = new ContainerViewController(activity, "containerId1", "containerName", new ContainerViewController.ContainerViewCreator() {
			@Override
			public ContainerViewController.ContainerView create(final Activity activity1, final String containerId, final String containerName) {
				return view;
			}
		}, initialNavigationOptions);
	}

	@Test
	public void applyNavigationOptionsHandlesNoParentStack() throws Exception {
		assertThat(uut.getParentStackController()).isNull();
		uut.onViewAppeared();
		assertThat(uut.getParentStackController()).isNull();
	}

	@Test
	public void initialOptionsAppliedOnAppear() throws Exception {
		assertThat(uut.getNavigationOptions()).isSameAs(initialNavigationOptions);
		initialNavigationOptions.title = "the title";
		StackController stackController = new StackController(activity, "stackId");
		stackController.push(uut);
		assertThat(stackController.getTopBar().getTitle()).isEmpty();

		uut.onViewAppeared();
		assertThat(stackController.getTopBar().getTitle()).isEqualTo("the title");
	}

	@Test
	public void mergeNavigationOptionsUpdatesCurrentOptions() throws Exception {
		assertThat(uut.getNavigationOptions().title).isEmpty();
		NavigationOptions options = new NavigationOptions();
		options.title = "new title";
		uut.mergeNavigationOptions(options);
		assertThat(uut.getNavigationOptions().title).isEqualTo("new title");
		assertThat(uut.getNavigationOptions()).isSameAs(initialNavigationOptions);
	}

	@Test
	public void reappliesOptionsOnMerge() throws Exception {
		StackController stackController = new StackController(activity, "stackId");
		stackController.push(uut);
		assertThat(stackController.getTopBar().getTitle()).isEmpty();

		NavigationOptions opts = new NavigationOptions();
		opts.title = "the new title";
		uut.mergeNavigationOptions(opts);

		assertThat(stackController.getTopBar().getTitle()).isEqualTo("the new title");
	}

	@Test
	public void appliesTopBackBackgroundColor() throws Exception {
		StackController stackController = new StackController(activity, "stackId");
		stackController.push(uut);
		assertThat(((ColorDrawable) stackController.getTopBar().getBackground()).getColor()).isNotEqualTo(Color.RED);

		NavigationOptions opts = new NavigationOptions();
		opts.topBarBackgroundColor = Color.RED;
		uut.mergeNavigationOptions(opts);

		assertThat(((ColorDrawable) stackController.getTopBar().getBackground()).getColor()).isEqualTo(Color.RED);
	}

	@Test
	public void appliesTopBarTextColor() throws Exception {
		assertThat(uut.getNavigationOptions()).isSameAs(initialNavigationOptions);
		initialNavigationOptions.title = "the title";
		StackController stackController = new StackController(activity, "stackId");
		stackController.push(uut);
		uut.onViewAppeared();
		assertThat(stackController.getTopBar().getTitleTextView().getCurrentTextColor()).isNotEqualTo(Color.RED);

		NavigationOptions opts = new NavigationOptions();
		opts.title = "the title";
		opts.topBarTextColor = Color.RED;
		uut.mergeNavigationOptions(opts);

		assertThat(stackController.getTopBar().getTitleTextView()).isNotEqualTo(null);
		assertThat(stackController.getTopBar().getTitleTextView().getCurrentTextColor()).isEqualTo(Color.RED);
	}

	@Test
	public void appliesTopBarTextSize() throws Exception {
		assertThat(uut.getNavigationOptions()).isSameAs(initialNavigationOptions);
		initialNavigationOptions.title = "the title";
		StackController stackController = new StackController(activity, "stackId");
		stackController.push(uut);
		uut.onViewAppeared();
		assertThat(stackController.getTopBar().getTitleTextView().getTextSize()).isNotEqualTo(18);

		NavigationOptions opts = new NavigationOptions();
		opts.title = "the title";
		opts.topBarTextFontSize = 18;
		uut.mergeNavigationOptions(opts);

		assertThat(stackController.getTopBar().getTitleTextView()).isNotEqualTo(null);
		assertThat(stackController.getTopBar().getTitleTextView().getTextSize()).isEqualTo(18);
	}
}