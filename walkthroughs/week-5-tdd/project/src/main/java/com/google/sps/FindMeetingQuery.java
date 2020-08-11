// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList();
    }

    if (getAllAttendees(request).isEmpty() || events.isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    Collection<TimeRange> freeTimes = getFreeTimes(events, request.getDuration(), getAllAttendees(request));

    if (freeTimes.isEmpty()) {
        freeTimes = getFreeTimes(events, request.getDuration(), request.getAttendees());
    }

    return freeTimes;
  }

  public Collection<String> getAllAttendees(MeetingRequest request) {
    Collection<String> allAttendees = new HashSet<>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    return allAttendees;
  }

  public Collection<TimeRange> getFreeTimes(Collection<Event> events, long duration, Collection<String> attendees) {
    Collection<TimeRange> eventsWithAttendees = filterByAttendees(events, attendees);
    Collection<TimeRange> freeTimes = splitDayByEvents(eventsWithAttendees);
    return filterByDuration(freeTimes, duration);
  }

  public Collection<TimeRange> filterByAttendees(Collection<Event> events, Collection<String> attendees) {
    return events.stream().filter(e -> (!Collections.disjoint(e.getAttendees(), attendees))).map(e -> e.getWhen()).collect(Collectors.toList());
  }

  public Collection<TimeRange> filterByDuration(Collection<TimeRange> times, long duration) {
    return times.stream().filter(c -> c.duration() >= duration).collect(Collectors.toList());
  }

  public Collection<TimeRange> splitDayByEvents(Collection<TimeRange> times) {
    Collection<TimeRange> sortedTimes = times.stream().sorted(TimeRange.ORDER_BY_START).collect(Collectors.toList());
    Collection<TimeRange> freeTimes = new LinkedList<>();

    int previousTime = TimeRange.START_OF_DAY;

    if (sortedTimes.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    for (TimeRange currentTime : sortedTimes) {
      if (previousTime < currentTime.end()) {
        if (previousTime < currentTime.start()) {
          freeTimes.add(TimeRange.fromStartEnd(previousTime, currentTime.start(), false));
        }
        previousTime = currentTime.end();
      }
    }

    if (previousTime < TimeRange.END_OF_DAY) {
      freeTimes.add(TimeRange.fromStartEnd(previousTime, TimeRange.END_OF_DAY, true));
    }
    
    return freeTimes;
  }
}
