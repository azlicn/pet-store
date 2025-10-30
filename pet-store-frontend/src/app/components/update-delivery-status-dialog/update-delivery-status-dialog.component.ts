import { Component, Inject } from "@angular/core";
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from "@angular/material/dialog";
import { FormControl, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { DeliveryStatus } from "../../models/delivery.model";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: "app-update-delivery-status-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatSelectModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
  ],
  templateUrl: "./update-delivery-status-dialog.component.html",
  styleUrls: ["./update-delivery-status-dialog.component.scss"],
})
export class UpdateDeliveryStatusDialogComponent {
  deliveryStatuses = Object.values(DeliveryStatus);
  statusControl = new FormControl(this.data.status);
  dateControl = new FormControl(this.data.date);
  timeControl = new FormControl("");

  constructor(
    public dialogRef: MatDialogRef<UpdateDeliveryStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { status: DeliveryStatus; date: string; orderNumber: string }
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    const dateValue = this.dateControl.value;
    const timeValue = this.timeControl.value;
    let formattedDate = null;
    if (dateValue) {
      const d = new Date(dateValue);
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      const timeString = timeValue ? timeValue : "00:00";
      formattedDate = `${yyyy}-${mm}-${dd}T${timeString}:00`;
    }
    this.dialogRef.close({
      status: this.statusControl.value,
      date: formattedDate,
    });
  }
}
