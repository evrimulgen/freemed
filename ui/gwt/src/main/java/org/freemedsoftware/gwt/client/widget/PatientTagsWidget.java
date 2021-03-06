/*
 * $Id$
 *
 * Authors:
 *      Jeff Buchbinder <jeff@freemedsoftware.org>
 *
 * FreeMED Electronic Medical Record and Practice Management System
 * Copyright (C) 1999-2012 FreeMED Software Foundation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.freemedsoftware.gwt.client.widget;

import static org.freemedsoftware.gwt.client.i18n.I18nUtil._;

import java.util.ArrayList;

import org.freemedsoftware.gwt.client.JsonUtil;
import org.freemedsoftware.gwt.client.Util;
import org.freemedsoftware.gwt.client.Util.ProgramMode;
import org.freemedsoftware.gwt.client.WidgetInterface;
import org.freemedsoftware.gwt.client.Module.PatientTagAsync;
import org.freemedsoftware.gwt.client.i18n.AppConstants;
import org.freemedsoftware.gwt.client.screen.PatientTagSearchScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PatientTagsWidget extends WidgetInterface {

	protected Integer patientId = new Integer(0);

	protected ArrayList<String> tags = null;

	protected final FlowPanel flowPanel;

	protected final PatientTagWidget wEntry;

	public PatientTagsWidget() {
		flowPanel = new FlowPanel();
		initWidget(flowPanel);

		wEntry = new PatientTagWidget();
		flowPanel.add(wEntry);
		wEntry.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				PatientTagWidget t = (PatientTagWidget) event.getSource();
				if (t.getTextEntryWidget().getText().length() > 2) {
					addTag(t.getText());
				}
			}
		});
	}

	/**
	 * Update database to set new tag.
	 * 
	 * @param tag
	 */
	public void addTag(final String tag) {
		if (Util.getProgramMode() == ProgramMode.STUBBED) {
			wEntry.clear();
			addTagToDisplay(tag);
		} else if (Util.getProgramMode() == ProgramMode.JSONRPC) {
			String[] params = { JsonUtil.jsonify(patientId), tag };
			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
					URL.encode(Util.getJsonRequest(
							"org.freemedsoftware.module.PatientTag.CreateTag",
							params)));
			try {
				builder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable ex) {
						Util.showInfoMsg("PatientTags", _("Failed to add tags."));
					}

					public void onResponseReceived(Request request,
							Response response) {
						if (200 == response.getStatusCode()) {
							Boolean r = (Boolean) JsonUtil.shoehornJson(
									JSONParser.parseStrict(response.getText()),
									"Boolean");
							if (r != null) {
								wEntry.clear();
								addTagToDisplay(tag);
								Util.showInfoMsg("PatientTags",_( "Tag added."));
							}
						} else {
							Util.showErrorMsg("PatientTags", _("Failed to add tags."));
						}
					}
				});
			} catch (RequestException e) {
				Util.showErrorMsg("PatientTags", _("Failed to add tags."));
			}
		} else {
			getProxy().CreateTag(patientId, tag, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean o) {
					wEntry.clear();
					addTagToDisplay(tag);
					Util.showInfoMsg("PatientTags", _("Tag added."));
				}

				public void onFailure(Throwable t) {
					GWT.log("Exception", t);
					Util.showErrorMsg("PatientTags", _("Failed to add tags."));
				}
			});
		}
	}

	/**
	 * Actual addition of tag to display.
	 * 
	 * @param tag
	 */
	protected void addTagToDisplay(String tag) {
		JsonUtil.debug("addTagToDisplay(" + tag + ")");
		HorizontalPanel p = new HorizontalPanel();
		p.setTitle(tag);
		final String oldTagName = tag.trim();
		final HTML r = new HTML("<sup>X</sup>");
		final Anchor tagLabel = new Anchor(tag);
		tagLabel.setStylePrimaryName("freemed-PatientTag");
		JsonUtil.debug("addclicklistener");
		tagLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent evt) {
				final Widget w = (Widget) evt.getSource();
				final PopupPanel p = new PopupPanel(true);
				final FlexTable fT = new FlexTable();
				fT.setWidget(0, 0, new HTML("<b>" + oldTagName + "</b>"));
				fT.setWidget(1, 0, new Label(_("Rename Tag")));
				final TextBox newTagName = new TextBox();
				fT.setWidget(2, 0, newTagName);
				final CustomButton changeTagButton = new CustomButton(_("Change"),AppConstants.ICON_MODIFY);
				fT.setWidget(2, 1, changeTagButton);
				changeTagButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent bEvt) {
						if (newTagName.getText().trim().length() > 0) {
							if (Util.getProgramMode() == ProgramMode.STUBBED) {
								// Stubbed mode
								p.hide();
								p.removeFromParent();
								((HorizontalPanel) w.getParent())
										.removeFromParent();
								addTagToDisplay(newTagName.getText().trim());
							} else if (Util.getProgramMode() == ProgramMode.JSONRPC) {
								String[] params = { oldTagName,
										newTagName.getText().trim() };
								RequestBuilder builder = new RequestBuilder(
										RequestBuilder.POST,
										URL
												.encode(Util
														.getJsonRequest(
																"org.freemedsoftware.module.PatientTag.ChangeTag",
																params)));
								try {
									builder.sendRequest(null,
											new RequestCallback() {
												public void onError(
														Request request,
														Throwable ex) {
												}

												public void onResponseReceived(
														Request request,
														Response response) {
													if (200 == response
															.getStatusCode()) {
														Boolean r = (Boolean) JsonUtil
																.shoehornJson(
																		JSONParser
																				.parseStrict(response
																						.getText()),
																		"Boolean");
														if (r != null) {
															p.hide();
															p
																	.removeFromParent();
															((HorizontalPanel) w
																	.getParent())
																	.removeFromParent();
															addTagToDisplay(newTagName
																	.getText()
																	.trim());
														}
													} else {
													}
												}
											});
								} catch (RequestException e) {
								}
							} else {
								getProxy().ChangeTag(oldTagName,
										newTagName.getText().trim(),
										new AsyncCallback<Boolean>() {
											public void onSuccess(Boolean o) {
												p.hide();
												p.removeFromParent();
												((HorizontalPanel) w
														.getParent())
														.removeFromParent();
												addTagToDisplay(newTagName
														.getText().trim());
											}

											public void onFailure(Throwable t) {
												GWT.log("Exception", t);
											}
										});
							}
						}
					}
				});
				final CustomButton searchButton = new CustomButton(_("Search"),AppConstants.ICON_SEARCH);
				fT.setWidget(2, 2, searchButton);
				searchButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent evt) {
						PatientTagSearchScreen searchScreen = new PatientTagSearchScreen();
						searchScreen.setTagValue(oldTagName);
						Util.spawnTab(_("Tag Search"), searchScreen);
						p.hide();
					}
				});
				p.add(fT);
				p.setPopupPosition(tagLabel.getAbsoluteLeft() + 5, tagLabel
						.getAbsoluteTop() - 10);
				p.setStyleName("freemed-PatientTagPopup");
				p.show();
			}
		});
		p.add(tagLabel);
		p.add(r);
		p.setStyleName("freemed-PatientTag");
		r.addStyleName("freemed-PatientTagRemove");
		r.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent evt) {
				if (Window.confirm("Are you sure you want to remove this tag?")) {
					HorizontalPanel container = (HorizontalPanel) ((Widget) evt
							.getSource()).getParent();
					removeTag(container.getTitle(), container);
				}
			}
		});
		flowPanel.add(p);
	}

	/**
	 * Update database to remove tag.
	 * 
	 * @param tag
	 */
	public void removeTag(String tag, final HorizontalPanel hp) {
		if (Util.getProgramMode() == ProgramMode.STUBBED) {
			hp.removeFromParent();
		} else if (Util.getProgramMode() == ProgramMode.JSONRPC) {
			String[] params = { JsonUtil.jsonify(patientId), tag };
			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
					URL.encode(Util.getJsonRequest(
							"org.freemedsoftware.module.PatientTag.ExpireTag",
							params)));
			try {
				builder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable ex) {
						Util.showErrorMsg("PatientTags", _("Failed to remove tag."));
					}

					public void onResponseReceived(Request request,
							Response response) {
						if (200 == response.getStatusCode()) {
							Boolean r = (Boolean) JsonUtil.shoehornJson(
									JSONParser.parseStrict(response.getText()),
									"Boolean");
							if (r != null) {
								hp.removeFromParent();
								Util.showInfoMsg("PatientTags", _("Tag removed."));
							}
						} else {
							Util.showErrorMsg("PatientTags", _("Failed to remove tag."));
						}
					}
				});
			} catch (RequestException e) {
				Util.showErrorMsg("PatientTags", _("Failed to remove tag."));
			}
		} else {
			getProxy().ExpireTag(patientId, tag, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean o) {
					hp.removeFromParent();
					Util.showErrorMsg("PatientTags", _("Tag removed."));
				}

				public void onFailure(Throwable t) {
					GWT.log("Exception", t);
					Util.showErrorMsg("PatientTags", _("Failed to remove tag."));
				}
			});
		}
	}

	/**
	 * Set internal patient id representation.
	 * 
	 * @param patient
	 */
	public void setPatient(Integer patient) {
		JsonUtil.debug("PatientTagsWidget.setPatient(" + patient.toString()
				+ ")");
		patientId = patient;
		populate();
	}

	/**
	 * Populate the widget via RPC.
	 */
	protected void populate() {
		if (Util.getProgramMode() == ProgramMode.STUBBED) {
			addTagToDisplay("testTag1");
			addTagToDisplay("Diabetes");
			addTagToDisplay("LatePayment");
		} else if (Util.getProgramMode() == ProgramMode.JSONRPC) {
			String[] params = { JsonUtil.jsonify(patientId) };
			RequestBuilder builder = new RequestBuilder(
					RequestBuilder.POST,
					URL
							.encode(Util
									.getJsonRequest(
											"org.freemedsoftware.module.PatientTag.TagsForPatient",
											params)));
			try {
				builder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable ex) {
					}

					public void onResponseReceived(Request request,
							Response response) {
						if (200 == response.getStatusCode()) {
							JsonUtil.debug(response.getText());
							String[] r = (String[]) JsonUtil.shoehornJson(
									JSONParser.parseStrict(response.getText()),
									"String[]");
							if (r != null) {
								for (int iter = 0; iter < r.length; iter++) {
									addTagToDisplay(r[iter]);
								}
							}
						} else {
						}
					}
				});
			} catch (RequestException e) {
			}
		} else {
			getProxy().TagsForPatient(patientId, new AsyncCallback<String[]>() {
				public void onSuccess(String[] tags) {
					for (int iter = 0; iter < tags.length; iter++) {
						addTagToDisplay(tags[iter]);
					}
				}

				public void onFailure(Throwable t) {
					GWT.log("Exception", t);
				}
			});
		}
	}

	/**
	 * Internal method to retrieve proxy object from Util.getProxy()
	 * 
	 * @return
	 */
	protected PatientTagAsync getProxy() {
		PatientTagAsync p = null;
		try {
			p = (PatientTagAsync) Util
					.getProxy("org.freemedsoftware.gwt.client.Module.PatientTag");
		} catch (Exception ex) {
			GWT.log("Exception", ex);
		}
		return p;
	}

}
