import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ITrip } from 'app/shared/model/trip.model';
import { getEntities as getTrips } from 'app/entities/trip/trip.reducer';
import { IGrade } from 'app/shared/model/grade.model';
import { getEntities as getGrades } from 'app/entities/grade/grade.reducer';
import { getEntity, updateEntity, createEntity, reset } from './export-prod.reducer';
import { IExportProd } from 'app/shared/model/export-prod.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ExportProdUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const trips = useAppSelector(state => state.trip.entities);
  const grades = useAppSelector(state => state.grade.entities);
  const exportProdEntity = useAppSelector(state => state.exportProd.entity);
  const loading = useAppSelector(state => state.exportProd.loading);
  const updating = useAppSelector(state => state.exportProd.updating);
  const updateSuccess = useAppSelector(state => state.exportProd.updateSuccess);
  const handleClose = () => {
    props.history.push('/export-prod');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getTrips({}));
    dispatch(getGrades({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.departuredate = convertDateTimeToServer(values.departuredate);

    const entity = {
      ...exportProdEntity,
      ...values,
      trip: trips.find(it => it.id.toString() === values.trip.toString()),
      grade: grades.find(it => it.id.toString() === values.grade.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          departuredate: displayDefaultDateTime(),
        }
      : {
          ...exportProdEntity,
          departuredate: convertDateTimeFromServer(exportProdEntity.departuredate),
          trip: exportProdEntity?.trip?.id,
          grade: exportProdEntity?.grade?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="accountingImportExportProductsApp.exportProd.home.createOrEditLabel" data-cy="ExportProdCreateUpdateHeading">
            <Translate contentKey="accountingImportExportProductsApp.exportProd.home.createOrEditLabel">
              Create or edit a ExportProd
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="export-prod-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('accountingImportExportProductsApp.exportProd.departuredate')}
                id="export-prod-departuredate"
                name="departuredate"
                data-cy="departuredate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="export-prod-trip"
                name="trip"
                data-cy="trip"
                label={translate('accountingImportExportProductsApp.exportProd.trip')}
                type="select"
              >
                <option value="" key="0" />
                {trips
                  ? trips.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="export-prod-grade"
                name="grade"
                data-cy="grade"
                label={translate('accountingImportExportProductsApp.exportProd.grade')}
                type="select"
              >
                <option value="" key="0" />
                {grades
                  ? grades.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/export-prod" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ExportProdUpdate;
