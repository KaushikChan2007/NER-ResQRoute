package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gis.ChatMessage
import com.example.gis.MessageSender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatbotSheet(
  messages: List<ChatMessage>,
  isSending: Boolean,
  onSendMessage: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Auto-scroll to bottom on new message
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val suggestedPrompts = listOf(
    "Book cold-chain insulin dispatch",
    "Check NH-6 Sonapur landslide status",
    "Reserve 20 oxygen cylinders for Silchar",
    "Can you suggest a safe detour?"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.88f)
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.HeadsetMic,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "NER Tactical Dispatch Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "Gemini Powered",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
            }
            Text(
              text = "Context-Aware AI • Multi-Step Logistics Bookings",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close chat")
        }
      }

      Spacer(Modifier.height(8.dp))

      // Message List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Intro welcome card if empty
        if (messages.isEmpty()) {
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = "Welcome to NER-ResQRoute Support",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "I remember our full conversation history to help you book multi-step supply dispatches, find verified relief depots, and navigate hazardous monsoonal road blocks across Assam, Meghalaya, and the Barak Valley.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        items(messages, key = { it.id }) { msg ->
          val isUser = msg.sender == MessageSender.USER
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
          ) {
            Surface(
              shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
              ),
              color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.widthIn(max = 300.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = msg.text,
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        if (isSending) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  Text("Analyzing conversation context...", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }
      }

      // Quick Suggestions Carousel
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        suggestedPrompts.forEach { prompt ->
          FilterChip(
            selected = false,
            onClick = { onSendMessage(prompt) },
            label = { Text(prompt, fontSize = 11.sp) },
            modifier = Modifier.testTag("chat_prompt_chip")
          )
        }
      }

      // Bottom Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = { Text("Ask about routes, or book a cargo dispatch...", fontSize = 13.sp) },
          modifier = Modifier
            .weight(1f)
            .testTag("chatbot_input_field"),
          shape = RoundedCornerShape(20.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          ),
          maxLines = 3
        )

        IconButton(
          onClick = {
            if (inputText.isNotBlank()) {
              val text = inputText.trim()
              inputText = ""
              onSendMessage(text)
            }
          },
          enabled = inputText.isNotBlank() && !isSending,
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .testTag("chatbot_send_button")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
